package App.Model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Assignment {
    private String citizenId;
    private String shelterId;
    private String timestamp;

    public Assignment(String citizenId, String shelterId) {
        this.citizenId = citizenId;
        this.shelterId = shelterId;
        this.timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public String getCitizenId() { return citizenId; }
    public String getShelterId() { return shelterId; }
    public String getAssignmentDate() { return timestamp; }
}