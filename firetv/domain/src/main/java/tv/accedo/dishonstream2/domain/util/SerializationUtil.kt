package tv.accedo.dishonstream2.domain.util

import android.util.Base64
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

class SerializationUtil {

    companion object{

        fun deserialize(s: String?): Any? {
            return ObjectInputStream(ByteArrayInputStream(Base64.decode(s, Base64.DEFAULT))).readObject()
        }

        fun serialize(o: Serializable?): String? {
            val bo = ByteArrayOutputStream()
            val so = ObjectOutputStream(bo)
            so.writeObject(o)
            so.flush()
            return Base64.encodeToString(bo.toByteArray(), Base64.DEFAULT)
        }
    }

}