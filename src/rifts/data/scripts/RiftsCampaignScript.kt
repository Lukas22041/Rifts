package rifts.data.scripts

import com.fs.starfarer.api.EveryFrameScript
import com.fs.starfarer.api.Global
import rifts.data.util.RiftStrings

class RiftsCampaignScript : EveryFrameScript
{
    override fun isDone(): Boolean {
        return false
    }

    override fun runWhilePaused(): Boolean {
        return false
    }

    override fun advance(amount: Float)
    {
        var playerfleet = Global.getSector().playerFleet

        if (playerfleet.isInHyperspace) return

        if (playerfleet.starSystem.hasTag(RiftStrings.DimensionalRift))
        {
            if (!playerfleet.starSystem.hasTag(RiftStrings.RiftExplored))
            {
                playerfleet.starSystem.addTag(RiftStrings.RiftExplored)
            }
        }

        if (playerfleet.hasAbility("fracture_jump"))
        {
            if (playerfleet.starSystem.hasTag(RiftStrings.DimensionalRift))
            {
                playerfleet.abilities.get("fracture_jump")!!.cooldownLeft = 1f
            }
        }
    }

}