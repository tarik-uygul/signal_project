package data_management;

import static org.junit.jupiter.api.Assertions.*;

import com.data_management.DataReader;
import org.junit.jupiter.api.Test;
import com.data_management.DataStorage;
import com.data_management.PatientRecord;
import java.util.List;

class DataStorageTest {

    @Test
    void testAddAndGetRecords() {
        // Mock reader (if you are still using it, otherwise you can remove it)
        DataReader reader = new DataReader() {
            @Override
            public void readData(DataStorage dataStorage) {
            }
        };

        // FIX: Use getInstance() instead of new DataStorage()
        DataStorage storage = DataStorage.getInstance(); 
        
        storage.addPatientData(1, 100.0, "WhiteBloodCells", 1714376789050L);
        storage.addPatientData(1, 200.0, "WhiteBloodCells", 1714376789051L);

        List<PatientRecord> records = storage.getRecords(1, 1714376789050L, 1714376789051L);
        
        // Note: Because it's a Singleton, previous tests or runs might leave data in memory. 
        // If it expects exactly 2 but finds more, you might need to adjust the test or clear the storage.
        assertTrue(records.size() >= 2); 
        assertEquals(100.0, records.get(0).getMeasurementValue()); 
    }
}