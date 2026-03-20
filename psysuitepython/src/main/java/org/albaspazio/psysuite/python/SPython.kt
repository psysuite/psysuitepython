package org.albaspazio.psysuite.python

import android.content.Context
import kotlin.reflect.full.memberProperties

import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform

/**
 * Singleton class for managing Python operations.
 *
 * @param ctx The Android context required to initialize Python.
 */
class SPython private constructor(ctx: Context?) {

    /**
     * The Python instance used for operations.
     */
    private val python: Python
        get() = Python.getInstance()

    companion object: SingletonHolder<SPython, Context?>(::SPython)

    init {
        if (! Python.isStarted()) {
            if(ctx == null)
                throw Exception("SPython: given context is null, cannot init Python")

            Python.start(AndroidPlatform(ctx))
            println("Python init complete")
        }
    }

    /**
     * Retrieves a Python module by its name.
     *
     * @param module The name of the Python module to retrieve.
     * @return The requested Python module.
     */
    fun getModule(module: String): PyObject {
        return python.getModule(module)
    }

    /**
     * Instantiates a Python class within a specified module.
     *
     * @param modulename The name of the Python module containing the class.
     * @param classname The name of the Python class to instantiate.
     * @param vararg params The parameters to pass to the class constructor.
     * @return The instantiated Python class.
     */
    fun instanciate(modulename: String, classname: String, vararg params: Any): PyObject {
        val module = getModule(modulename)
        return module.callAttr(classname, *params)
    }

    /**
     * Converts a Kotlin data class instance to a Python dictionary.
     *
     * @param instance The Kotlin data class instance to convert.
     * @return A Python dictionary representing the instance.
     */
    fun class2dict(instance: Any): PyObject {
        var qp = arrayOf<Any>()
        instance::class.memberProperties.map {
            qp += arrayOf(it.name, it.call(instance))
        }
        return python.builtins.callAttr("dict", qp)
    }
}

// found in https://medium.com/@BladeCoder/kotlin-singletons-with-argument-194ef06edd9e
open class SingletonHolder<out T: Any, in A>(creator: (A) -> T) {
    private var creator: ((A) -> T)? = creator
    @Volatile private var instance: T? = null

    /**
     * Returns the singleton instance of the specified type, creating it if necessary.
     *
     * @param arg The argument to pass to the singleton's constructor.
     * @return The singleton instance of the specified type.
     */
    fun getInstance(arg: A): T {

        return instance ?: synchronized(this) {

            val i2 = instance
            if (i2 != null) {
                i2
            } else {
                val created = creator!!(arg)
                instance = created
                creator = null
                created
            }
        }
    }
}