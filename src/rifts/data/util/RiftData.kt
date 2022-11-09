package rifts.data.util

import com.fs.starfarer.api.Global

object RiftData
{
    val strangeMatterID = "strange_matter"
    val languageSampleID = "language_sample"
    val exoticConstructionID = "exotic_construction"

    val wormholeEntity = "wormholeEntity"
    val DimensionalRift = "DimensionalRift"
    val hasWormhole = "hasWormhole"
    val languageLearned = "languageLearned"

    val RiftExplored = "RiftExplored"

    //Planet Tags
    val RiftPlanet = "RiftPlanet"


    //Arkship Tags
    val ArkshipInactive = "ArkshipInactive"
    val ArkshipWarped = "ArkshipWarped"


    //Memory
    val riftSpecMemoryKey = "\$riftSpecMemoryKey"

    var arkshipName: String
        get()
        {
            return Global.getSector().memoryWithoutUpdate.getString("\$Rifts_ArkshipName")
        }
        set(value) =  Global.getSector().memoryWithoutUpdate.set("\$Rifts_ArkshipName", value)

    var arkshipPrefix: String
        get()
        {
            return Global.getSector().memoryWithoutUpdate.getString("\$Rifts_ArkshipPrefix")
        }
        set(value) = Global.getSector().memoryWithoutUpdate.set("\$Rifts_ArkshipPrefix", value)
}

