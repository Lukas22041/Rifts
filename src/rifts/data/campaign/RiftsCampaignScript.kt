package rifts.data.campaign

import com.fs.starfarer.api.EveryFrameScript
import com.fs.starfarer.api.Global
import rifts.data.util.RiftData

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

        if (playerfleet.starSystem.hasTag(RiftData.DimensionalRift))
        {
            if (!playerfleet.starSystem.hasTag(RiftData.RiftExplored))
            {
                playerfleet.starSystem.addTag(RiftData.RiftExplored)
            }
        }

        if (playerfleet.hasAbility("fracture_jump"))
        {
            if (playerfleet.starSystem.hasTag(RiftData.DimensionalRift))
            {
                playerfleet.abilities.get("fracture_jump")!!.cooldownLeft = 1f
            }
        }
    }

}