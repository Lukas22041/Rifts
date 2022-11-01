package rifts.data.shipsystems

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.combat.*
import com.fs.starfarer.api.graphics.SpriteAPI
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript
import com.fs.starfarer.api.plugins.ShipSystemStatsScript
import org.lazywizard.lazylib.MathUtils
import org.lazywizard.lazylib.combat.CombatUtils
import java.awt.Color
import java.util.*
import kotlin.collections.ArrayList


class SuperconductorSystem() : BaseShipSystemScript(), CombatLayeredRenderingPlugin
{


    var ship: ShipAPI? = null
    var color = Color(255,255,255,255)
    var effectLevel = 0f
    var init = false

    override fun apply(stats: MutableShipStatsAPI?, id: String?, state: ShipSystemStatsScript.State?, effectLevel: Float) {
        ship = stats!!.entity as ShipAPI
        var engine = Global.getCombatEngine()
        this.effectLevel = effectLevel
        if (!init)
        {
            engine.addLayeredRenderingPlugin(this)
            init = true
        }
        var projectiles = ArrayList<MissileAPI>()


        color = Color(0, 255, 50, 255)


        ship!!.engineController.fadeToOtherColor(this, color, Color(color.red, color.green, color.blue, 150), 1f, effectLevel * 0.5f)
        ship!!.setJitterUnder(this, color, effectLevel, 0, 0f, 0f);

        for (projectile in engine.missiles)
        {
            if (projectile == null) continue
            if (projectile.weapon == null) continue
            if (projectile.weapon.ship != ship) continue
            if (projectile.weapon.id != "rifts_inductor") continue

            projectiles.add(projectile)
        }

        for (projectile in projectiles)
        {
            if (projectile.customData.get("already_empd: ${projectile}") == true)
            {
                //projectile.projectileSpec.fringeColor = Misc.getHighlightColor()
                return
            }
            projectile.engineController.fadeToOtherColor(this, color, color, 1f, 1f)


            var smallestDistance = 10000f
            var target: ShipAPI? = null
            var targets = CombatUtils.getShipsWithinRange(projectile.location, 500f)
            for (tar in targets)
            {
                if (!tar.isAlive) continue
                if (tar.owner == ship!!.owner) continue
                var distance = MathUtils.getDistance(projectile.location, tar!!.location)
                if (distance < smallestDistance)
                {
                    smallestDistance = distance
                    target = tar
                }
            }


            if (Math.random().toFloat() > 0.98f && target is ShipAPI) {
                val emp = 100f
                val dam = projectile!!.damageAmount / 2
                engine!!.spawnEmpArc(projectile!!.source, projectile.location, projectile, target, DamageType.ENERGY, dam, emp,  // emp
                    600f,  // max range
                    "tachyon_lance_emp_impact", 20f,  // thickness
                    color, Color(255, 255, 255, 255))
                projectile.customData.put("already_empd: ${projectile}", true)

            }
        }
    }

    override fun init(entity: CombatEntityAPI?) {

    }

    override fun cleanup() {

    }

    override fun isExpired(): Boolean {
        return false
    }

    override fun advance(amount: Float) {

    }

    override fun getActiveLayers(): EnumSet<CombatEngineLayers> {
        return EnumSet.of(CombatEngineLayers.ABOVE_SHIPS_LAYER)
    }

    override fun getRenderRadius(): Float {
        return 30000f
    }

    var glowSprite: SpriteAPI = Global.getSettings().getSprite("rifts", "rifts_phenix_glow");

    override fun render(layer: CombatEngineLayers?, viewport: ViewportAPI?)
    {
        if (ship == null)    return
        glowSprite.setAdditiveBlend()
        glowSprite.color = Color(0, 200, 40, 255)
        glowSprite.alphaMult = effectLevel
        glowSprite.setSize(ship!!.spriteAPI.width, ship!!.spriteAPI.height)
        glowSprite.angle = ship!!.spriteAPI.angle
        glowSprite.renderAtCenter(ship!!.location.x , ship!!.location.y)
    }
}