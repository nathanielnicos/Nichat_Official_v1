package id.nns.nichat.utils.converters

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import id.nns.nichat.domain.model.Message

object ChatConverters {

    fun fromJsonToList(json: String) : List<Message> {
        val listType = object: TypeToken<List<Message>>(){}.type
        return Gson().fromJson(json, listType)
    }

    fun fromListToJson(list: List<Message>) : String {
        return Gson().toJson(list)
    }

}