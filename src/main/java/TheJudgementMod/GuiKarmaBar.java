package TheJudgementMod;

import org.lwjgl.opengl.GL11;
import com.google.common.eventbus.Subscribe;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiKarmaBar extends Gui {
	private Minecraft mc;
	private static final ResourceLocation texturepath = new ResourceLocation("TheJudgementMod", "textures/gui/karma_bar.png");

	public GuiKarmaBar(Minecraft mc) {
		super();
		this.mc = mc;
	}

	@SubscribeEvent(priority = EventPriority.NORMAL)
public void onRenderExperienceBar(RenderGameOverlayEvent event)
{
if (event.isCancelable() || event.type != ElementType.EXPERIENCE)
{
return;
}


ExtendedPlayer props = ExtendedPlayer.get(this.mc.thePlayer);

if (props == null || props.getMaxKarma() == 0)
{
return;
}

int xPos = 2;
int yPos = 2;

GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

GL11.glDisable(GL11.GL_LIGHTING);

this.mc.getTextureManager().bindTexture(texturepath);

this.drawTexturedModalRect(xPos, yPos, 0, 0, 51, 4);

int karmabarwidth = (int)(((float) props.getCurrentKarma() / props.getMaxKarma()) * 49);
System.out.println("[GUI Karma] Current karma bar width: " + karmabarwidth);

this.drawTexturedModalRect(xPos, yPos + 1, 0, 4, karmabarwidth, 2);
}
}