package org.example.restapiscrapper.common;

import org.example.restapiscrapper.service.PollingService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.core.io.Resource;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import static org.mockito.Mockito.*;

class PollingStarterTest {

    private PollingService pollingService;
    private PollingStarter pollingStarter;

    void setUp(String urls) throws Exception {
        pollingService = mock(PollingService.class);
        Resource resource = mock(Resource.class);

        AppProperties appProperties = new AppProperties();
        appProperties.setNames(List.of("A", "C"));

        pollingStarter = new PollingStarter(pollingService, appProperties);

        InputStream inputStream = new ByteArrayInputStream(urls.getBytes());
        when(resource.getInputStream()).thenReturn(inputStream);

        ReflectionTestUtils.setField(pollingStarter, "sourceFileResource", resource);
    }

    @Test
    void testOnlyMatchingNamesArePolled() throws Exception {
        setUp("""
            A https://a.com
            B https://b.com
            C https://c.com
            """);
        pollingStarter.run();

        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        verify(pollingService, times(2)).startPolling(captor.capture());

        List<String> polled = captor.getAllValues();
        assert polled.contains("https://a.com");
        assert polled.contains("https://c.com");
        assert !polled.contains("https://b.com");
    }

    @Test
    void testEmptyUrlsPoll() throws Exception {
        setUp("");
        pollingStarter.run();
        verifyNoInteractions(pollingService);
    }

    @Test
    void testBrokenUrlsPoll() throws Exception {
        setUp("akfdfkkgkekoekf");
        pollingStarter.run();
        verifyNoInteractions(pollingService);
    }
}
