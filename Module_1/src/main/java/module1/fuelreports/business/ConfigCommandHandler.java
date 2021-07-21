package module1.fuelreports.business;

import com.beust.jcommander.Parameter;

public class ConfigCommandHandler {

    @Parameter(
            names = "--data-dir",
            description = "report/config/process",
            required = true
    )
    private String path;

    public String getPath() {
        return path;
    }
}
