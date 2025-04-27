package slimeknights.tconstruct.tools.common.client.renderer;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;

import slimeknights.tconstruct.library.client.renderer.RenderProjectileBase;
import slimeknights.tconstruct.tools.common.entity.EntityArrow;

public class RenderArrow extends RenderProjectileBase<EntityArrow> {

  public RenderArrow(RenderManager renderManager) {
    super(renderManager);
  }

  @Override
  protected void customCustomRendering(EntityArrow entity, double x, double y, double z, float entityYaw, float partialTicks) {
    if(!entity.inGround) {
      entity.roll += entity.rollSpeed * partialTicks;
    }
    float r = entity.roll;

    GlStateManager.rotate(r, 0f, 0f, 1f);
  }
}
