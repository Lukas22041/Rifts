package lunalib.util

import com.fs.starfarer.api.Global
import com.fs.starfarer.api.campaign.FactionAPI
import com.fs.starfarer.api.campaign.PlanetAPI
import com.fs.starfarer.api.campaign.SectorEntityToken
import com.fs.starfarer.api.campaign.StarSystemAPI
import com.fs.starfarer.api.campaign.econ.MarketAPI
import com.fs.starfarer.api.impl.campaign.ids.Conditions
import com.fs.starfarer.api.impl.campaign.ids.Industries
import com.fs.starfarer.api.impl.campaign.ids.Submarkets
import com.fs.starfarer.api.impl.campaign.procgen.*
import com.fs.starfarer.api.util.WeightedRandomPicker
import org.lazywizard.lazylib.MathUtils


object LunaProcGen
{

    fun generatePlanet(system: StarSystemAPI, id: String, name: String, radius: Float): PlanetAPI
    {
        return generatePlanet(system, id, name, radius, null)
    }

    @JvmStatic
    fun generatePlanet(system: StarSystemAPI, id: String, name: String, radius: Float, orbitSpeed: Float?) : PlanetAPI
    {
        val planetID: String = id
        val planetName: String = name
        val orbitRadius = radius
        var orbitDays: Float = orbitRadius / (20f + MathUtils.getRandomNumberInRange(0f,1f) * 5f)
        if (orbitSpeed != null)
        {
            orbitDays = orbitSpeed
        }
        val orbitAngle: Float = MathUtils.getRandomNumberInRange(0f,1f) * 360f

        val categoryData = categoryPicker(system, radius, false)
        val planetData = planetPicker(system, categoryData)

        var planet = system.addPlanet(planetID, system.center, planetName, planetData.id, orbitAngle, MathUtils.getRandomNumberInRange(planetData.minRadius, planetData.maxRadius), orbitRadius, orbitDays)
        if (system.star != null)
        {
            PlanetConditionGenerator.generateConditionsForPlanet(planet, StarAge.ANY)
        }

        return planet
    }

    @JvmStatic
    fun generateMoon(targetPlanet: SectorEntityToken, id: String, name: String, radius: Float, moonSizeDivider: Float) : PlanetAPI?
    {
        val moonID: String = id
        val moonName: String = name
        val orbitRadius = radius
        val orbitDays = MathUtils.getRandomNumberInRange(50f,100f)
        val orbitAngle: Float = MathUtils.getRandomNumberInRange(0f,1f) * 360f

        val categoryData = categoryPicker(targetPlanet.starSystem, targetPlanet.circularOrbitRadius, true)
        val planetData = planetPicker(targetPlanet.starSystem, categoryData)

        var moon = targetPlanet.starSystem.addPlanet(moonID, targetPlanet, moonName, planetData.id, orbitAngle, MathUtils.getRandomNumberInRange(planetData.minRadius / moonSizeDivider, planetData.maxRadius / moonSizeDivider), orbitRadius, orbitDays)
        if (targetPlanet.starSystem.star != null)
        {
            PlanetConditionGenerator.generateConditionsForPlanet(moon, StarAge.ANY)
        }

        return moon
    }

    @JvmStatic
    fun generateSpecificMarket(targetPlanet: PlanetAPI, size: Int, factionID: String) : MarketAPI
    {
        return generateMarket(targetPlanet, size, factionID)
    }

    @JvmStatic
    fun generateRandomMarket(targetPlanet: PlanetAPI, size: Int) : MarketAPI
    {
        return generateMarket(targetPlanet, size, null)
    }

    @JvmStatic
    private fun generateMarket(targetPlanet: PlanetAPI, size: Int, factionID: String?): MarketAPI
    {
        val economy = Global.getSector().economy
        val planetID: String = targetPlanet.id
        val marketID = planetID + "_market"

        val faction: FactionAPI
        val conditions = targetPlanet.market.conditions
        //gets a random Faction for the new Market
        if (factionID == null)
        {
            val factions: MutableList<FactionAPI> = ArrayList()
            var markets = Global.getSector().economy.marketsCopy
            for (i in markets.indices) {
                if (markets[i].faction !== Global.getSector().playerFleet.getFaction()) factions.add(markets[i].faction)
            }
            faction = factions.get(MathUtils.getRandomNumberInRange(0, factions.size - 1));

        }
        else
        {
            faction = Global.getSector().getFaction(factionID)
        }


        //creates the market
        var market: MarketAPI = Global.getFactory().createMarket(marketID, targetPlanet.name, size)
        market.setFactionId(faction.id)
        market.setPrimaryEntity(targetPlanet)
        market.getTariff().modifyFlat("generator", 0.3f);

        //adds the Conditions of the planet to the new market
        for (condition in conditions)
        {
            when (size)
            {
                1 -> market.addCondition(Conditions.POPULATION_1)
                2 -> market.addCondition(Conditions.POPULATION_2)
                3 -> market.addCondition(Conditions.POPULATION_3)
                4 -> market.addCondition(Conditions.POPULATION_4)
                5 -> market.addCondition(Conditions.POPULATION_5)
                6 -> market.addCondition(Conditions.POPULATION_6)
                7 -> market.addCondition(Conditions.POPULATION_7)
                8 -> market.addCondition(Conditions.POPULATION_8)
                9 -> market.addCondition(Conditions.POPULATION_9)
                10 -> market.addCondition(Conditions.POPULATION_10)
            }
            market.addCondition(condition)
        }
        market.isHidden = false
        market.surveyLevel = MarketAPI.SurveyLevel.FULL
        //Adds the industries to the new market

        val habitable: Boolean = market.hasCondition(Conditions.HABITABLE)

        var picker: WeightedRandomPicker<String> = WeightedRandomPicker<String>()
        var Industrypicker: WeightedRandomPicker<String> = WeightedRandomPicker<String>()
        market.addIndustry(Industries.POPULATION)
        if (size <= 4) { market.addIndustry(Industries.SPACEPORT); Industrypicker.add(Industries.HEAVYINDUSTRY, 0.75f) }
        if (size >= 5) { market.addIndustry(Industries.MEGAPORT); Industrypicker.add(Industries.ORBITALWORKS, 0.75f) }
        picker.add(Industries.WAYSTATION, 1f)
        picker.add(Industries.GROUNDDEFENSES, 1f)

        if (size in 0..3) picker.add(Industries.PATROLHQ, 1f)
        if (size in 4..5) { Industrypicker.add(Industries.MILITARYBASE, 2f); picker.add(Industries.ORBITALSTATION, 2f) }
        if (size in 6..10) { Industrypicker.add(Industries.HIGHCOMMAND, 2f); picker.add(Industries.BATTLESTATION, 2f) }

        for (condition in market.conditions)
        {
            condition.isSurveyed = true
        }

        for (condition in market.conditions)
        {
            when (condition.id)
            {
                Conditions.WATER_SURFACE -> Industrypicker.add(Industries.AQUACULTURE, 1f)
                Conditions.HABITABLE -> Industrypicker.add("commerce", 0.75f)

                Conditions.FARMLAND_POOR -> Industrypicker.add(Industries.FARMING, 0.5f)
                Conditions.FARMLAND_ADEQUATE -> Industrypicker.add(Industries.FARMING, 1f)
                Conditions.FARMLAND_RICH -> Industrypicker.add(Industries.FARMING, 1.5f)
                Conditions.FARMLAND_BOUNTIFUL -> Industrypicker.add(Industries.FARMING, 2f)

                Conditions.RARE_ORE_SPARSE -> Industrypicker.add(Industries.MINING, 0.5f)
                Conditions.RARE_ORE_MODERATE -> Industrypicker.add(Industries.MINING, 1f)
                Conditions.RARE_ORE_ABUNDANT -> Industrypicker.add(Industries.MINING, 1f)
                Conditions.RARE_ORE_RICH -> Industrypicker.add(Industries.MINING, 1.5f)
                Conditions.RARE_ORE_ULTRARICH -> Industrypicker.add(Industries.MINING, 2f)

                Conditions.ORE_SPARSE -> Industrypicker.add(Industries.MINING, 0.5f)
                Conditions.ORE_MODERATE -> Industrypicker.add(Industries.MINING, 1f)
                Conditions.ORE_ABUNDANT -> Industrypicker.add(Industries.MINING, 1f)
                Conditions.ORE_RICH -> Industrypicker.add(Industries.MINING, 1.5f)
                Conditions.ORE_ULTRARICH -> Industrypicker.add(Industries.MINING, 2f)

                Conditions.ORGANICS_COMMON -> Industrypicker.add(Industries.MINING, 0.75f)
                Conditions.ORGANICS_PLENTIFUL -> Industrypicker.add(Industries.MINING, 1f)
                Conditions.ORGANICS_ABUNDANT -> Industrypicker.add(Industries.MINING, 2f)

                Conditions.VOLATILES_TRACE -> Industrypicker.add(Industries.MINING, 0.5f)
                Conditions.VOLATILES_DIFFUSE -> Industrypicker.add(Industries.MINING, 1f)
                Conditions.VOLATILES_PLENTIFUL -> Industrypicker.add(Industries.MINING, 1f)
                Conditions.VOLATILES_ABUNDANT -> Industrypicker.add(Industries.MINING, 2f)

                Conditions.RUINS_SCATTERED -> Industrypicker.add(Industries.TECHMINING, 0.5f)
                Conditions.RUINS_WIDESPREAD -> Industrypicker.add(Industries.TECHMINING, 1f)
                Conditions.RUINS_EXTENSIVE -> Industrypicker.add(Industries.TECHMINING, 1f)
                Conditions.RUINS_VAST -> Industrypicker.add(Industries.TECHMINING, 1.5f)
            }
        }

        Industrypicker.add(Industries.REFINING, 0.75f)
        Industrypicker.add(Industries.LIGHTINDUSTRY, 0.75f)
        Industrypicker.add(Industries.FUELPROD, 0.75f)

        val industriesToAdd: Int = when (size)
        {
            1 -> 1
            2 -> 1
            3 -> 1
            4 -> 2
            5 -> 3
            6 -> 4
            else -> 4
        }
        val randomIndusties = MathUtils.getRandomNumberInRange(1, industriesToAdd)
        val randomNonIndustries = size / 2

        market.addSubmarket(Submarkets.SUBMARKET_OPEN)

        for (index in 0 until randomIndusties)
        {
            if (picker.isEmpty) break
            market.addIndustry(Industrypicker.pickAndRemove())
        }
        for (index in 0 until randomNonIndustries)
        {
            if (picker.isEmpty) break
            market.addIndustry(picker.pickAndRemove())
        }

        market.addSubmarket(Submarkets.SUBMARKET_BLACK)
        //adds the market to the economy and planet
        economy.addMarket(market, true)
        targetPlanet.setMarket(market);
        targetPlanet.setFaction(faction.id);

        return market
    }

    private fun categoryPicker(system: StarSystemAPI, radius: Float, isMoon: Boolean) : CategoryGenDataSpec
    {
        val picker: WeightedRandomPicker<CategoryGenDataSpec> = WeightedRandomPicker<CategoryGenDataSpec>()
        val categoryDataSpecs = Global.getSettings().getAllSpecs(CategoryGenDataSpec::class.java)

        for (obj in categoryDataSpecs)
        {
            val categoryData = obj as CategoryGenDataSpec
            val catNothing = categoryData.getCategory() == "cat_nothing"
            val catGiant = categoryData.getCategory() == "cat_giant"
            val catLava= categoryData.getCategory() == "cat_lava"
            val catFrozen = categoryData.getCategory() == "cat_frozen"
            val catHab3 = categoryData.getCategory() == "cat_hab3"
            val catHab4 = categoryData.getCategory() == "cat_hab4"
            val catHab5 = categoryData.getCategory() == "cat_hab5"
            val catTerrain = categoryData.category == "cat_terrain_lagrange"
            val catMagfield = categoryData.category == "cat_magfield"
            val catTerrainAccretion = categoryData.category == "cat_terrain_accretion"
            val catTerrainRing = categoryData.category == "cat_terrain_rings"
            val catTerrainLagrange = categoryData.category == "cat_terrain_lagrange"

            if (isMoon && catGiant) continue
            if (catNothing || catTerrain || catMagfield || catTerrainAccretion || catTerrainRing || catTerrainLagrange || catHab5) continue
            if (radius <= 3000 && catHab3 || catHab4) continue
            if (radius <= 6000 && catGiant || catFrozen) continue
            if (radius > 3000 && catLava) continue

            var weight = categoryData.frequency
            var multiplier: Float = 1f
            if (system.star != null)  multiplier = categoryData.getMultiplier(system.star.typeId)
            var modWeight = weight * multiplier

            picker.add(categoryData, modWeight)
        }
        val pick = picker.pick()

        return pick
    }

    private fun planetPicker(system: StarSystemAPI, categoryData: CategoryGenDataSpec): PlanetGenDataSpec
    {
        val planetDataSpecs = Global.getSettings().getAllSpecs(PlanetGenDataSpec::class.java)
        val picker: WeightedRandomPicker<PlanetGenDataSpec> = WeightedRandomPicker<PlanetGenDataSpec>()
        for (obj in planetDataSpecs)
        {
            val planetData = obj as PlanetGenDataSpec
            if (planetData.category != categoryData.getCategory()) continue

            val weight = planetData.frequency

            picker.add(planetData, weight)

        }


        val pickedPlanet: PlanetGenDataSpec = picker.pick()
        return pickedPlanet

    }
}