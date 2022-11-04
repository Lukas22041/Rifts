package rifts.data.campaign.procgen.world

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.impl.campaign.ids.Tags
import com.fs.starfarer.api.impl.campaign.procgen.NebulaEditor
import com.fs.starfarer.api.impl.campaign.terrain.HyperspaceTerrainPlugin
import com.fs.starfarer.api.util.Misc
import org.lazywizard.lazylib.MathUtils
import rifts.data.scripts.OriginEvent
import rifts.data.util.RiftStrings
import java.awt.Color


object OriginSystem
{
    fun generate()
    {
        var sector = Global.getSector()
        var system = sector.createStarSystem("Origin System")

        system.addTag(RiftStrings.DimensionalRift)
        system.addTag(Tags.SYSTEM_CUT_OFF_FROM_HYPER)

        system.location.set(-20000f, 10000f)

        var star = system.initStar("Origin", "origin_star", 200f, 50f)
        system.lightColor = star.spec.atmosphereColor

        system.addRingBand(system.center, "misc", "rings_asteroids0", 256f, 0, Color.gray, 256f, 1100f, 100f);


        var script = OriginEvent(star)
        system.addScript(script)

        val plugin = Misc.getHyperspaceTerrain().plugin as HyperspaceTerrainPlugin
        val editor = NebulaEditor(plugin)
        val minRadius = plugin.tileSize * 2f

        var jumppoint = Global.getFactory().createJumpPoint("originJumpoint", "Hyperspace Jumpoint")
        jumppoint.setCircularOrbitPointingDown(star, 180f, 700f, 100f)
        star.starSystem.addEntity(jumppoint)
        system.autogenerateHyperspaceJumpPoints(true, false)

        val radius = system.maxRadiusInHyperspace
        editor.clearArc(system.location.x, system.location.y, 0f, radius + minRadius, 0f, 360f)
        editor.clearArc(system.location.x, system.location.y, 0f, radius + minRadius, 0f, 360f, 0.25f)
    }
}