package rifts.data.campaign.procgen.specs

import com.fs.starfarer.api.Global
import org.apache.log4j.Level
import org.json.JSONArray
import kotlin.collections.ArrayList

object SpecLoader
{

    @JvmStatic
    var RiftsSpecs: MutableList<RiftSpec> = ArrayList()
    @JvmStatic
    var StarTypeSpecs: MutableList<StarTypeSpec> = ArrayList()

    private val log = Global.getLogger(SpecLoader::class.java)
    init {
        log.level = Level.ALL
    }

    @JvmStatic
    fun loadRiftSpec()
    {
        val riftsSpec: JSONArray = Global.getSettings().getMergedSpreadsheetDataForMod("rift_id", "data/campaign/procgen/rifts_gen_data.csv", "rifts")
        val script = Global.getSettings().scriptClassLoader

        var specs: MutableList<RiftSpec> = ArrayList()
        for (index in 0 until riftsSpec.length())
        {
            val rows = riftsSpec.getJSONObject(index)

            val id= rows.getString("rift_id");
            val weight =rows.getString("weight").toFloat()
            val starType= rows.getString("starType")

            val unfilteredBackgrounds = rows.getString("backgrounds").split(",");
            val backgrounds: MutableList<String> = ArrayList()

            for (background in unfilteredBackgrounds)
            {
                backgrounds.add(background.trim())
            }

            val path = rows.getString("script");
            val riftGenScript: Class<*> = script.loadClass(path);

            RiftsSpecs.add(RiftSpec(id, weight, starType, backgrounds, riftGenScript));
        }
    }

    fun loadStarTypeSpec()
    {
        val starTypeSpec: JSONArray = Global.getSettings().getMergedSpreadsheetDataForMod("starType", "data/campaign/procgen/rifts_star_type.csv", "rifts")
        val script = Global.getSettings().scriptClassLoader

        var specs: MutableList<RiftSpec> = ArrayList()
        for (index in 0 until starTypeSpec.length())
        {
            val rows = starTypeSpec.getJSONObject(index)

            val id= rows.getString("starType");
            val unfilteredStars = rows.getString("stars").split(",");
            val stars: MutableList<String> = ArrayList()

            for (star in unfilteredStars)
            {
                stars.add(star.trim())
            }

            StarTypeSpecs.add(StarTypeSpec(id, stars));
        }
    }
}