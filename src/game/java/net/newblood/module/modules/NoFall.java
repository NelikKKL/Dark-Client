package net.newblood.module.modules;

import net.minecraft.client.entity.EntityPlayerSP;
import net.newblood.module.Module;

/**
 * Removes fall damage by never reporting to the server that you left the
 * ground.
 *
 * Fall damage is computed server-side (EntityLivingBase#fall, called when
 * the server's own copy of your player transitions from airborne to
 * onGround), based entirely on the onGround flag your client reports in
 * its movement packets - it has nothing to do with the client's own
 * fallDistance field. The old version zeroed fallDistance locally, which
 * only affects client-side rendering/prediction and never reached the
 * server, so damage still applied. This hooks EntityPlayerSP's packet
 * sender (see spoofOnGround, called from onUpdateWalkingPlayer) and makes
 * it always claim onGround=true whenever this module is enabled - the
 * server then never sees a "was falling, now landed" transition.
 *
 * Real client-side collision/physics are untouched (EntityPlayerSP#onGround
 * itself is not modified), so movement still looks and feels normal - only
 * the network report is spoofed.
 */
public class NoFall extends Module {

	private static volatile boolean enabledStatic;

	public NoFall() {
		super("NoFall", "Removes fall damage (never reports leaving the ground to the server)", Category.MOVEMENT);
	}

	@Override
	public void onEnable() {
		enabledStatic = true;
	}

	@Override
	public void onDisable() {
		enabledStatic = false;
	}

	/** Called from EntityPlayerSP#onUpdateWalkingPlayer for every outgoing movement packet. */
	public static boolean spoofOnGround(EntityPlayerSP player, boolean actualOnGround) {
		return enabledStatic ? true : actualOnGround;
	}
}
