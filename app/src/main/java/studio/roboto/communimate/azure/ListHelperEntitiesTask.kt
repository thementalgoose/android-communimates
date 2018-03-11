package studio.roboto.communimate.azure

import android.os.AsyncTask
import com.microsoft.azure.storage.CloudStorageAccount
import com.microsoft.azure.storage.StorageException
import com.microsoft.azure.storage.table.CloudTable
import com.microsoft.azure.storage.table.CloudTableClient
import com.microsoft.azure.storage.table.TableQuery
import studio.roboto.communimate.azure.azure_entities.HelperEntity
import timber.log.Timber

class ListHelperEntitiesTask(val listener: ListHelperEntitiesTaskListener) : AsyncTask<Unit, Unit, Unit>() {

    lateinit var tableClient: CloudTableClient
    lateinit var table: CloudTable

    var success = true
    lateinit var entities: MutableIterable<HelperEntity>

    companion object {
        const val TABLE_NAME: String = "HelperKeys"
    }

    override fun doInBackground(vararg p0: Unit?) {

        val azureAccount = CloudStorageAccount.parse(AzureConstants.HELPER_KEYS_CONNECTION_STRING)

        tableClient = azureAccount.createCloudTableClient()

        table = tableClient.getTableReference(TABLE_NAME)

        listEntities()
    }

    override fun onPostExecute(result: Unit?) {
        if (success) {
            listener.success(entities)
        } else {
            listener.failure()
        }
    }

    private fun listEntities() {
        try {
            entities = table.execute(TableQuery.from(HelperEntity::class.java))
            for (entity in entities) {
                Timber.d(entity.partitionKey.toString() + " - " + entity.rowKey.toString())
            }
        } catch (e: StorageException) {
            success = false

            Timber.e("List entities exception " + e.message)
        }
    }

    interface ListHelperEntitiesTaskListener {

        /**
         * Caution! Iterating through these on the main thread will cause a crash!
         * */
        fun success(list: Iterable<HelperEntity>)

        fun failure()
    }
}