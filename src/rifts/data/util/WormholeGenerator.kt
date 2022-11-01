package rifts.data.util

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.SectorEntityToken
import java.awt.Color

object WormholeGenerator
{

    var wormholeID = 0
    fun createOneWayWormhole(RiftLocation: SectorEntityToken, target: SectorEntityToken, color: Color): SectorEntityToken
    {
        if (Global.getSector().memoryWithoutUpdate.get("\$rifts_WormholeCount") != null) wormholeID = Global.getSector().memoryWithoutUpdate.getInt("\$rifts_WormholeCount")

        wormholeID += 1
        val Rift: SectorEntityToken = RiftLocation.starSystem.addCustomEntity("Rift_Wormhole_$wormholeID","Dimensional Rift","dimensional_rift","independent")
        Global.getSector().memoryWithoutUpdate.set("\$rifts_WormholeCount", wormholeID)
        Rift.memoryWithoutUpdate.set("\$WormholeDestination", target)
        Rift.memoryWithoutUpdate.set("\$WormholeColor", color)
        Rift.addTag(RiftStrings.wormholeEntity)

        return Rift
    }

    fun createTwoWayWormhole(RiftLocation1: SectorEntityToken, RiftLocation2: SectorEntityToken, color: Color): MutableList<SectorEntityToken>
    {
        if (Global.getSector().memoryWithoutUpdate.get("\$rifts_WormholeCount") != null) wormholeID = Global.getSector().memoryWithoutUpdate.getInt("\$rifts_WormholeCount")

        val Rift1: SectorEntityToken = RiftLocation1.starSystem.addCustomEntity("Rift_Wormhole$wormholeID","Dimensional Rift","dimensional_rift","independent")
        wormholeID += 1
        val Rift2: SectorEntityToken = RiftLocation2.starSystem.addCustomEntity("Rift_Wormhole$wormholeID","Dimensional Rift","dimensional_rift","independent")
        wormholeID += 1
        Global.getSector().memoryWithoutUpdate.set("\$rifts_WormholeCount", wormholeID)
        Rift1.memoryWithoutUpdate.set("\$WormholeDestination", Rift2)
        Rift2.memoryWithoutUpdate.set("\$WormholeDestination", Rift1)
        Rift1.memoryWithoutUpdate.set("\$WormholeColor", color)
        Rift2.memoryWithoutUpdate.set("\$WormholeColor", color)
        Rift1.addTag(RiftStrings.wormholeEntity)
        Rift2.addTag(RiftStrings.wormholeEntity)

        var rifts: MutableList<SectorEntityToken> = ArrayList()
        rifts.add(Rift1)
        rifts.add(Rift2)

        return rifts
    }
}