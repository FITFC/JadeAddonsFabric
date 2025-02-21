package snownee.jade.addon.create;

import org.jetbrains.annotations.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import snownee.jade.addon.core.ObjectNameProvider;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.Identifiers;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElement;
import snownee.jade.impl.WailaClientRegistration;

public enum ContraptionExactBlockProvider implements IEntityComponentProvider {
	INSTANCE;

	private long time = Long.MIN_VALUE;
	private BlockState hitBlock;
	private BlockHitResult hitResult;

	@Override
	public @Nullable IElement getIcon(EntityAccessor accessor, IPluginConfig config, IElement currentIcon) {
		if (!validate()) {
			return null;
		}
		ItemStack stack = hitBlock.getBlock().getCloneItemStack(accessor.getLevel(), hitResult.getBlockPos(), hitBlock);
		return CreatePlugin.client.getElementHelper().item(stack);
	}

	@Override
	public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config) {
		if (!validate()) {
			return;
		}
		Component name = null;
		if (WailaClientRegistration.INSTANCE.shouldPick(hitBlock)) {
			ItemStack pick = hitBlock.getBlock().getCloneItemStack(accessor.getLevel(), hitResult.getBlockPos(), hitBlock);
			if (pick != null && !pick.isEmpty())
				name = pick.getHoverName();
		}
		if (name == null) {
			String key = hitBlock.getBlock().getDescriptionId();
			if (I18n.exists(key)) {
				name = hitBlock.getBlock().getName();
			} else {
				ItemStack pick = accessor.getPickedResult();
				if (pick != null && !pick.isEmpty()) {
					name = pick.getHoverName();
				} else {
					name = new TextComponent(key);
				}
			}
		}
		tooltip.remove(Identifiers.CORE_OBJECT_NAME);
		tooltip.add(0, config.getWailaConfig().getFormatting().title(name.getString()).copy().withStyle(ChatFormatting.ITALIC), Identifiers.CORE_OBJECT_NAME);
	}

	public void setHit(BlockHitResult hitResult, BlockState hitBlock) {
		this.hitResult = hitResult;
		this.hitBlock = hitBlock;
		time = Util.getMillis();
	}

	private boolean validate() {
		return (Util.getMillis() - time) < 10;
	}

	@Override
	public ResourceLocation getUid() {
		return CreatePlugin.CONTRAPTION_EXACT_BLOCK;
	}

	@Override
	public int getDefaultPriority() {
		return ObjectNameProvider.INSTANCE.getDefaultPriority() + 1;
	}

}
