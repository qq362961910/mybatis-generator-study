package plugin;

import org.mybatis.generator.api.PluginAdapter;

import java.util.List;

public class SqlMapPlugin extends PluginAdapter {

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }


}
