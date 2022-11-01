package rifts.data.weapons.projectiles

import com.fs.starfarer.api.combat.*
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI
import org.lwjgl.util.vector.Vector2f
import java.awt.Color


class PhotonBlasterOnHit : OnHitEffectPlugin {
    override fun onHit(projectile: DamagingProjectileAPI?, target: CombatEntityAPI?, point: Vector2f?, shieldHit: Boolean, damageResult: ApplyDamageResultAPI?, engine: CombatEngineAPI?)
    {
        if (Math.random().toFloat() > 0.90f && !shieldHit && target is ShipAPI) {
            val emp = projectile!!.empAmount
            val dam = projectile!!.damageAmount
            engine!!.spawnEmpArc(projectile!!.source, point, target, target, DamageType.ENERGY, dam, emp,  // emp
                100000f,  // max range
                "tachyon_lance_emp_impact", 20f,  // thickness
                Color(25, 100, 155, 255), Color(255, 255, 255, 255))

            //engine.spawnProjectile(null, null, "plasma", point, 0, new Vector2f(0, 0));
        }
    }
}