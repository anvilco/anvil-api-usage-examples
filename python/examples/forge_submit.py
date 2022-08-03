from typing import Optional

from python_anvil.api_resources.mutations import BaseQuery
from python_anvil.api_resources.payload import BaseModel

DEFAULT_RESPONSE_QUERY = """
{
  id
  eid
  payloadValue
  currentStep
  completedAt
  createdAt
  updatedAt
  signer {
    name
    email
    status
    routingOrder
  }
  weldData {
    id
    eid
    isTest
    isComplete
    agents
  }
}
"""

FORGE_SUBMIT = """
    mutation ForgeSubmit(
        $forgeEid: String!,
        $weldDataEid: String,
        $submissionEid: String,
        $payload: JSON!,
        $currentStep: Int,
        $complete: Boolean,
        $isTest: Boolean,
        $timezone: String,
        $groupArrayId: String,
        $groupArrayIndex: Int,
        $errorType: String,
    ) {{
        forgeSubmit (
            forgeEid: $forgeEid,
            weldDataEid: $weldDataEid,
            submissionEid: $submissionEid,
            payload: $payload,
            currentStep: $currentStep,
            complete: $complete,
            isTest: $isTest,
            timezone: $timezone,
            groupArrayId: $groupArrayId,
            groupArrayIndex: $groupArrayIndex,
            errorType: $errorType
        ) {query}
    }}
"""
