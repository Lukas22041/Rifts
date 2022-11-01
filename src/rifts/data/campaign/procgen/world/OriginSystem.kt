package rifts.data.campaign.procgen.world

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.impl.campaign.ids.Tags
import com.fs.starfarer.api.impl.campaign.procgen.NebulaEditor
import com.fs.starfarer.api.impl.campaign.terrain.HyperspaceTerrainPlugin
import com.fs.starfarer.api.util.Misc
import rifts.data.scripts.OriginEvent
import rifts.data.util.RiftStrings


object OriginSystem
{
    fun generate()
    {
        var sector = Global.getSector()
        var system = sector.createStarSystem("Origin System")

        system.addTag(RiftStrings.DimensionalRift)
        system.addTag(Tags.SYSTEM_CUT_OFF_FROM_HYPER)

        system.location.set(-20000f, 10000f)

        var star = system.initStar("Origin", "origin_star", 100f, 0f)
        system.lightColor = star.spec.atmosphereColor

        system.autogenerateHyperspaceJumpPoints(true, false)

        var script = OriginEvent(star)
        system.addScript(script)

        val plugin = Misc.getHyperspaceTerrain().plugin as HyperspaceTerrainPlugin
        val editor = NebulaEditor(plugin)
        val minRadius = plugin.tileSize * 2f

        val radius = system.maxRadiusInHyperspace
        editor.clearArc(system.location.x, system.location.y, 0f, radius + minRadius, 0f, 360f)
        editor.clearArc(system.location.x, system.location.y, 0f, radius + minRadius, 0f, 360f, 0.25f)
    }
}