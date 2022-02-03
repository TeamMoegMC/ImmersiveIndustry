package com.teammoeg.immersiveindustry.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.teammoeg.immersiveindustry.IIMain;

import net.minecraft.client.gui.IBidiRenderer;
import net.minecraft.client.gui.screen.DatapackFailureScreen;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
/**
 * Forge make this datapack failure screen not actually shows that mod loading is failed.
 * So we add these codes to provide information if this is our fault.
 * */
@Mixin(DatapackFailureScreen.class)
public abstract class DatapackFailureScreenMixin extends Screen {

	public DatapackFailureScreenMixin(ITextComponent titleIn) {
		super(titleIn);
	}
	@Shadow
	private IBidiRenderer field_243284_a;
	@Inject(at=@At("TAIL"),method="init")
	public void ii$init(CallbackInfo cbi) {
		if(IIMain.loadfailed)
			this.field_243284_a = IBidiRenderer.func_243258_a(this.font,this.getTitle().copyRaw().appendSibling(new TranslationTextComponent("gui.immersiveindustry.failed_datapack")), this.width - 50);
	}
	
}
