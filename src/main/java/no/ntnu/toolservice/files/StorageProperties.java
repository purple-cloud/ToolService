package no.ntnu.toolservice.files;

import no.ntnu.toolservice.ToolServiceApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@ConfigurationProperties("storage")
public class StorageProperties {

    // Folder location for storing files
    private String location = "images";

    public String getLocation() {
        System.out.println(location);
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

}
