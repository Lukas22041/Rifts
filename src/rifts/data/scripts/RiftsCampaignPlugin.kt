package rifts.data.scripts

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.PluginPick
import com.fs.starfarer.api.campaign.BaseCampaignPlugin
import com.fs.starfarer.api.campaign.CampaignPlugin
import com.fs.starfarer.api.campaign.InteractionDialogPlugin
import com.fs.starfarer.api.campaign.JumpPointAPI.JumpDestination
import com.fs.starfarer.api.campaign.SectorEntityToken
import rifts.data.campaign.interaction.ArkshipInteraction
import rifts.data.campaign.interaction.FirstWormholeInteraction
import rifts.data.campaign.interaction.RiftPlanetInteraction
import rifts.data.util.RiftStrings

class RiftsCampaignPlugin : BaseCampaignPlugin()
{

    override fun isTransient(): Boolean {
        return true
    }

    override fun pickInteractionDialogPlugin(interactionTarget: SectorEntityToken?): PluginPick<InteractionDialogPlugin>? {

        if (interactionTarget == null) return null
        if (interactionTarget.hasTag(RiftStrings.RiftPlanet))
        {
            return PluginPick<InteractionDialogPlugin>(RiftPlanetInteraction(), CampaignPlugin.PickPriority.HIGHEST)
        }

        if (interactionTarget.id.contains("Rift_Wormhole"))
        {
            if (Global.getSector().memoryWithoutUpdate.get("\$RiftHasBeenUsed") == null)
            {
                return PluginPick<InteractionDialogPlugin>(FirstWormholeInteraction(), CampaignPlugin.PickPriority.HIGHEST)
            }
            else
            {
                Global.getSector().doHyperspaceTransition(Global.getSector().playerFleet, interactionTarget, JumpDestination(interactionTarget.memoryWithoutUpdate.get("\$WormholeDestination") as SectorEntityToken, "debug") , 0.1f);
            }
        }
        if (interactionTarget.id == "Arkship")
        {
            return PluginPick<InteractionDialogPlugin>(ArkshipInteraction(), CampaignPlugin.PickPriority.HIGHEST)
        }

        return null

    }
}