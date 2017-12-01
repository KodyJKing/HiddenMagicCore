package hiddenmagiccore;

import com.google.common.eventbus.EventBus;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;

public class Dummy extends DummyModContainer {

    public Dummy() {
        super(new ModMetadata());
        ModMetadata md = getMetadata();
        md.modId = "hiddenmagiccore";
        md.name = "Hidden Magic Core";
        md.version = "1.0";
        md.credits = "Me!";
        md.authorList = Arrays.asList("kjk");
        md.description = "A core mod for Hidden Magic";
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        bus.register(this);
        return true;
    }

    @SubscribeEvent
    public void modConstruction(FMLConstructionEvent evt) {}

    @SubscribeEvent
    public void init(FMLInitializationEvent evt) {}

    @SubscribeEvent
    public void preInit(FMLPreInitializationEvent evt) {}

    @SubscribeEvent
    public void postInit(FMLPostInitializationEvent evt) {}

}
