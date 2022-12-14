package rifts.data.campaign.interaction.planets

import lunalib.extension.LunaInteraction

class PlanetRuins_NoRuins() : LunaInteraction()
{
    override fun mainPage()
    {
        addPara("The fleet arrives at a planet within the Rift. Due to its location, it's impossible to create a Colony here.")
        addLeaveOption()
    }

    override fun optionSelected(optionText: String?, optionData: Any?) {
        super.optionSelected(optionText, optionData)
    }
}

