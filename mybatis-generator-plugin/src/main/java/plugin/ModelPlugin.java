package plugin;

import org.mybatis.generator.api.PluginAdapter;

import java.util.List;

public class ModelPlugin extends PluginAdapter {

    public boolean validate(List<String> warnings) {
        return true;
    }


}
