package studio.roboto.communimate.azure.azure_entities

import com.microsoft.azure.storage.table.TableServiceEntity

public class HelperEntity: TableServiceEntity {

    constructor() : super()

    constructor(partitionKey: String?, rowKey: String?) : super(partitionKey, rowKey)
}