package data_management;

import com.data_management.DataStorage;
import com.data_management.PatientRecord;
import com.data_management.WebSocketDataReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URISyntaxException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WebSocketDataReaderTest {
    private DataStorage storage;
    private WebSocketDataReader reader;

    @BeforeEach
    void setUp() throws URISyntaxException {
        // We use a dummy URI because we won't actually call connect() in this test
        reader = new WebSocketDataReader("ws://localhost:8080");
        storage = DataStorage.getInstance();
        
        // We inject the storage manually so we can test the parsing directly
        try {
            reader.readData(storage); 
        } catch (Exception e) {
            // Ignore connection errors since the server isn't running in the test environment
        }
    }

    @Test
    void testParseValidData() {
        // Simulate receiving a message from the WebSocket server
        String mockMessage = "1,1700000000000,HeartRate,75.0";
        reader.onMessage(mockMessage);

        List<PatientRecord> records = storage.getRecords(1, 1600000000000L, 1800000000000L);
        assertFalse(records.isEmpty(), "Storage should contain the parsed record.");
        assertEquals(75.0, records.get(records.size() - 1).getMeasurementValue());
    }

    @Test
    void testParsePercentageData() {
        // Saturation data comes in with a "%" sign
        String mockMessage = "2,1700000000000,Saturation,98%";
        reader.onMessage(mockMessage);

        List<PatientRecord> records = storage.getRecords(2, 1600000000000L, 1800000000000L);
        assertFalse(records.isEmpty());
        assertEquals(98.0, records.get(records.size() - 1).getMeasurementValue());
    }

    @Test
    void testParseTriggeredAlert() {
        // Alerts come in as "triggered" or "resolved"
        String mockMessage = "3,1700000000000,Alert,triggered";
        reader.onMessage(mockMessage);

        List<PatientRecord> records = storage.getRecords(3, 1600000000000L, 1800000000000L);
        assertFalse(records.isEmpty());
        assertEquals(1.0, records.get(records.size() - 1).getMeasurementValue()); // 1.0 = triggered
    }

    @Test
    void testMalformedDataDoesNotCrash() {
        // This should trigger the catch block in onMessage but NOT crash the program
        String malformedMessage = "This,Is,Not,Valid,Data";
        assertDoesNotThrow(() -> reader.onMessage(malformedMessage));
    }
}
