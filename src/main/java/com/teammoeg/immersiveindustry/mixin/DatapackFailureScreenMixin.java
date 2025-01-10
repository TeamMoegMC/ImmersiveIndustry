package com.teammoeg.immersiveindustry.mixin;

import net.minecraft.client.gui.screens.DatapackLoadFailureScreen;
import org.spongepowered.asm.mixin.Mixin;
/**
 * Forge make this datapack failure screen not actually shows that mod loading is failed.
 * So we add these codes to provide information if this is our fault.
 * */
@Mixin(DatapackLoadFailureScreen.class)
public abstract class DatapackFailureScreenMixin {

//	public DatapackFailureScreenMixin(ITextComponent titleIn) {
//		super(titleIn);
//	}
//	@Shadow
//	private IBidiRenderer field_243284_a;
//	@Inject(at=@At("TAIL"),method="init")
//	public void ii$init(CallbackInfo cbi) {
//		if(IIMain.loadfailed)
//			this.field_243284_a = IBidiRenderer.func_243258_a(this.font,this.getTitle().copyRaw().appendSibling(new TranslationTextComponent("gui.immersiveindustry.failed_datapack")), this.width - 50);
//	}
	
}
