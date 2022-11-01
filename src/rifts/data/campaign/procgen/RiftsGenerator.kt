package rifts.data.campaign.procgen

import com.fs.starfarer.api.util.WeightedRandomPicker
import rifts.data.campaign.procgen.specs.RiftSpec
import rifts.data.campaign.procgen.specs.SpecLoader
import rifts.data.campaign.procgen.specs.StarTypeSpec

object RiftsGenerator
{

    var randomRiftsAmount = 10

    fun spawnAllRifts()
    {
        generateRift("arkship_rift")
        generateRift("chirality_main")
        generateRift("wormhole_intersection_rift")

        //Generates Random Rifts
        for (i in 0 until randomRiftsAmount)
        {
           generateRift()
        }

    }

    /**
     *Generates a random Rift when called. Use the Overload with a String to spawn a specific Rift
     */
    fun generateRift()
    {
        generateRift("none")
    }

    /**
     *Generates a Rift with the ID Given.
     */
    fun generateRift(riftID: String)
    {
        var riftSpec: RiftSpec = selectRiftType(riftID)
        var riftStarSpec: StarTypeSpec = selectRiftStarSpec(riftSpec) ?: return

        var newRift: RiftGenAPI = riftSpec.RiftsGenScript.newInstance() as RiftGenAPI
        newRift.generate(riftSpec, riftStarSpec)
    }

    private fun selectRiftType(riftID: String) : RiftSpec
    {
        var picker: WeightedRandomPicker<RiftSpec> = WeightedRandomPicker()
        var specs: List<RiftSpec> = SpecLoader.RiftsSpecs

        for (spec in specs)
        {
            if (riftID == "none")
            {
                picker.add(spec, spec.RiftWeight)
            }
            else if (spec.RiftSpecID == riftID)
            {
                picker.add(spec, 1f)
            }
        }

        return picker.pick()
    }

    private fun selectRiftStarSpec(riftSpec: RiftSpec) : StarTypeSpec?
    {
        var specs: List<StarTypeSpec> = SpecLoader.StarTypeSpecs

        for (spec in specs)
        {
            if (spec.StarTypeID == riftSpec.RiftStarType)
            {
                return spec
            }
        }
        return null
    }

}