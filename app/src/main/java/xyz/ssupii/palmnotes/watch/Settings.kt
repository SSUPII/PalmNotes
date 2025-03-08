package xyz.ssupii.palmnotes.watch

class Settings private constructor(){
    init {
        throw IllegalStateException("Class should be instances")
    }

    companion object {
        private val settings = mutableListOf(
            //Defaults
            "setting_filename_hide" to true,
            "setting_big_title" to false,
            "setting_serif_title" to true,
            "setting_contents_hide" to false
        )

        fun get(): List<Pair<String, Boolean>>{
            return settings
        }

        fun get(position: Int): Pair<String, Boolean>{
            return settings[position]
        }

        fun set(position: Int, newValue: Boolean){
            settings[position] = Pair(settings[position].first, newValue)
        }

        fun size(): Int{
            return settings.size
        }
    }

}