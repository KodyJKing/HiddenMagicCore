package hiddenmagiccore;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import javax.annotation.Nullable;
import java.util.Map;

@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.TransformerExclusions({"hiddenmagiccore"})

public class Plugin implements IFMLLoadingPlugin {

    @Override
    public String[] getASMTransformerClass() {
        return new String[]{ Transformer.class.getName() };
    }

    @Override
    public String getModContainerClass() {
        return Dummy.class.getName();
    }

    @Nullable
    @Override
    public String getSetupClass() { return null; }

    @Override
    public void injectData(Map<String, Object> data) { }

    @Override
    public String getAccessTransformerClass() { return null; }
}
