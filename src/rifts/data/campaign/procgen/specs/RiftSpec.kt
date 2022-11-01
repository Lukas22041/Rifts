package rifts.data.campaign.procgen.specs

data class RiftSpec(
    val RiftSpecID: String,
    val RiftWeight: Float,
    val RiftStarType: String,
    val RiftBackgrounds: List<String>,
    val RiftsGenScript: Class<*>
)
