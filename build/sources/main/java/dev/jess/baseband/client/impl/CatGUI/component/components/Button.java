package dev.jess.baseband.client.impl.CatGUI.component.components;


import dev.jess.baseband.client.api.Main.BaseBand;
import dev.jess.baseband.client.api.Module.Module;
import dev.jess.baseband.client.api.Settings.Setting;
import dev.jess.baseband.client.impl.CatGUI.CatGUI;
import dev.jess.baseband.client.impl.CatGUI.component.Component;
import dev.jess.baseband.client.impl.CatGUI.component.Frame;
import dev.jess.baseband.client.impl.CatGUI.component.components.sub.Checkbox;
import dev.jess.baseband.client.impl.CatGUI.component.components.sub.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.ArrayList;

public class Button extends Component {

	private final ArrayList<Component> subcomponents;
	private final int height;
	public Module mod;
	public Frame parent;
	public int offset;
	public boolean open;
	private boolean isHovered;

	public Button(Module mod, Frame parent, int offset) {
		this.mod = mod;
		this.parent = parent;
		this.offset = offset;
		this.subcomponents = new ArrayList<Component>();
		this.open = false;
		height = 12;
		int opY = offset + 12;
		if (BaseBand.settingsManager.getSettingsByMod(mod) != null) {
			for (Setting s : BaseBand.settingsManager.getSettingsByMod(mod)) {
				if (s.isCombo()) {
					this.subcomponents.add(new ModeButton(s, this, mod, opY));
					opY += 12;
				}
				if (s.isSlider()) {
					this.subcomponents.add(new Slider(s, this, opY));
					opY += 12;
				}
				if (s.isCheck()) {
					this.subcomponents.add(new Checkbox(s, this, opY));
					opY += 12;
				}
			}
		}
		this.subcomponents.add(new Keybind(this, opY));
		this.subcomponents.add(new VisibleButton(this, mod, opY));
	}

	@Override
	public void setOff(int newOff) {
		offset = newOff;
		int opY = offset + 12;
		for (Component comp : this.subcomponents) {
			comp.setOff(opY);
			opY += 12;
		}
	}

	@Override
	public void renderComponent() {
		Gui.drawRect(parent.getX(), this.parent.getY() + this.offset, parent.getX() + parent.getWidth(), this.parent.getY() + 12 + this.offset, this.isHovered ? (this.mod.isToggled() ? new Color(0xFF9700FF).darker().getRGB() : 0xFF9700FF) : (this.mod.isToggled() ? new Color(14, 14, 14).getRGB() : 0xFF111111));
		GL11.glPushMatrix();
		GL11.glScalef(0.5f, 0.5f, 0.5f);
		Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(this.mod.getName(), (parent.getX() + 2) * 2, (parent.getY() + offset + 2) * 2 + 4, this.mod.isToggled() ? 0xBD62FF : - 1);
		if (this.subcomponents.size() > 2)
			Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(this.open ? "-" : "+", (parent.getX() + parent.getWidth() - 10) * 2, (parent.getY() + offset + 2) * 2 + 4, - 1);
		GL11.glPopMatrix();
		if (this.open) {
			if (! this.subcomponents.isEmpty()) {
				for (Component comp : this.subcomponents) {
					comp.renderComponent();
				}
				Gui.drawRect(parent.getX() + 2, parent.getY() + this.offset + 12, parent.getX() + 3, parent.getY() + this.offset + ((this.subcomponents.size() + 1) * 12), CatGUI.color);
			}
		}
	}

	@Override
	public int getHeight() {
		if (this.open) {
			return (12 * (this.subcomponents.size() + 1));
		}
		return 12;
	}

	@Override
	public void updateComponent(int mouseX, int mouseY) {
		this.isHovered = isMouseOnButton(mouseX, mouseY);
		if (! this.subcomponents.isEmpty()) {
			for (Component comp : this.subcomponents) {
				comp.updateComponent(mouseX, mouseY);
			}
		}
	}

	@Override
	public void mouseClicked(int mouseX, int mouseY, int button) {
		if (isMouseOnButton(mouseX, mouseY) && button == 0) {
			this.mod.setToggled(! mod.isToggled());
		}
		if (isMouseOnButton(mouseX, mouseY) && button == 1) {
			this.open = ! this.open;
			this.parent.refresh();
		}
		for (Component comp : this.subcomponents) {
			comp.mouseClicked(mouseX, mouseY, button);
		}
	}

	@Override
	public void mouseReleased(int mouseX, int mouseY, int mouseButton) {
		for (Component comp : this.subcomponents) {
			comp.mouseReleased(mouseX, mouseY, mouseButton);
		}
	}

	@Override
	public void keyTyped(char typedChar, int key) {
		for (Component comp : this.subcomponents) {
			comp.keyTyped(typedChar, key);
		}
	}

	public boolean isMouseOnButton(int x, int y) {
		return x > parent.getX() && x < parent.getX() + parent.getWidth() && y > this.parent.getY() + this.offset && y < this.parent.getY() + 12 + this.offset;
	}
}
