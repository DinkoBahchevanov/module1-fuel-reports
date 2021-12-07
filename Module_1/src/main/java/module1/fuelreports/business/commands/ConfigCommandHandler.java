package module1.fuelreports.business.commands;

import com.beust.jcommander.Parameter;

public class ConfigCommandHandler {

    @Parameter(
            names = "--data-dir",
            description = "given path for creating a directory",
            required = true
    )
    private String path;

    public String getPath() {
        return path;
    }
}
