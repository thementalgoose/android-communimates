package studio.roboto.communimate.azure

import android.os.AsyncTask
import com.microsoft.azure.storage.CloudStorageAccount
import com.microsoft.azure.storage.StorageException
import com.microsoft.azure.storage.table.CloudTable
import com.microsoft.azure.storage.table.CloudTableClient
import com.microsoft.azure.storage.table.TableOperation
import studio.roboto.communimate.azure.azure_entities.HelperEntity
import timber.log.Timber

class InsertHelperEntityTask(val listener: InsertHelperEntityTaskListener) : AsyncTask<HelperEntity, Unit, Unit>() {

    lateinit var tableClient: CloudTableClient
    lateinit var table: CloudTable

    var success = true

    companion object {
        const val TABLE_NAME: String = "HelperKeys"
    }

    override fun doInBackground(vararg helperEntities: HelperEntity?) {

        val azureAccount = CloudStorageAccount.parse(AzureConstants.HELPER_KEYS_CONNECTION_STRING)

        tableClient = azureAccount.createCloudTableClient()

        table = tableClient.getTableReference(TABLE_NAME)

        insertEntity(helperEntities.first()!!)
    }

    override fun onPostExecute(result: Unit?) {
        if (success) {
            listener.success()
        } else {
            listener.failure()
        }
    }

    private fun insertEntity(helperEntity: HelperEntity) {

        val insertOperation: TableOperation = TableOperation.insert(helperEntity)

        try {
            table.execute(insertOperation)
        } catch (e: StorageException) {
            success = false

            Timber.e("Insert entity exception " + e.message)
        }
    }

    interface InsertHelperEntityTaskListener {

        fun success()

        fun failure()
    }
}