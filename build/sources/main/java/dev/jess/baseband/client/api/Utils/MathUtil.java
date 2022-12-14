package dev.jess.baseband.client.api.Utils;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class MathUtil {
	public static double degToRad(double deg) {
		return deg * (float) (Math.PI / 180.0f);
	}

	public static Vec3d direction(float yaw) {
		return new Vec3d(Math.cos(degToRad(yaw + 90f)), 0, Math.sin(degToRad(yaw + 90f)));
	}

	public static Vec3d interpolateEntity(Entity entity, float time) {
		return new Vec3d(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * time,
				entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * time,
				entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * time);
	}

	public static float[] calcAngle(Vec3d from, Vec3d to) {
		double difX = to.x - from.x;
		double difY = (to.y - from.y) * - 1.0;
		double difZ = to.z - from.z;
		double dist = MathHelper.sqrt(difX * difX + difZ * difZ);
		return new float[]{(float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0), (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difY, dist)))};
	}

	public static double[] directionSpeed(double speed) {
		final Minecraft mc = Minecraft.getMinecraft();
		float forward = mc.player.movementInput.moveForward;
		float side = mc.player.movementInput.moveStrafe;
		float yaw = mc.player.prevRotationYaw
				+ (mc.player.rotationYaw - mc.player.prevRotationYaw) * mc.getRenderPartialTicks();

		if (forward != 0) {
			if (side > 0) {
				yaw += (forward > 0 ? - 45 : 45);
			} else if (side < 0) {
				yaw += (forward > 0 ? 45 : - 45);
			}
			side = 0;

			// forward = clamp(forward, 0, 1);
			if (forward > 0) {
				forward = 1;
			} else if (forward < 0) {
				forward = - 1;
			}
		}

		final double sin = Math.sin(Math.toRadians(yaw + 90));
		final double cos = Math.cos(Math.toRadians(yaw + 90));
		final double posX = (forward * speed * cos + side * speed * sin);
		final double posZ = (forward * speed * sin - side * speed * cos);
		return new double[]{posX, posZ};
	}


	public static double roundNumber(double number, int scale) {
		BigDecimal bigDecimal = new BigDecimal(number);
		bigDecimal = bigDecimal.setScale(scale, RoundingMode.HALF_UP);

		return bigDecimal.doubleValue();
	}

}
