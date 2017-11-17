package com.elavon.converge.model.mapper;

import com.elavon.converge.model.ElavonTransactionRequest;
import com.elavon.converge.model.type.ElavonEntryMode;
import com.elavon.converge.model.type.ElavonPosMode;
import com.elavon.converge.model.type.ElavonTransactionType;
import com.elavon.converge.util.CardUtil;
import com.elavon.converge.util.CurrencyUtil;

import javax.inject.Inject;

import co.poynt.api.model.EntryMode;
import co.poynt.api.model.FundingSourceEntryDetails;
import co.poynt.api.model.Transaction;

public class MsrMapper implements InterfaceMapper {

    @Inject
    public MsrMapper() {
    }

    @Override
    public ElavonTransactionRequest createAuth(final Transaction transaction) {
        final ElavonTransactionRequest request = createRequest(transaction);
        request.setTransactionType(ElavonTransactionType.AUTH_ONLY);
        return request;
    }

    @Override
    public ElavonTransactionRequest createCapture(final Transaction transaction) {
        throw new RuntimeException("Please implement");
    }

    @Override
    public ElavonTransactionRequest createVoid(final Transaction transaction) {
        final ElavonTransactionRequest request = createRequest(transaction);
        request.setTransactionType(ElavonTransactionType.VOID);
        return request;
    }

    @Override
    public ElavonTransactionRequest createOfflineAuth(final Transaction transaction) {
        throw new RuntimeException("Please implement");
    }

    @Override
    public ElavonTransactionRequest createRefund(final Transaction transaction) {
        final ElavonTransactionRequest request = createRequest(transaction);
        request.setTransactionType(ElavonTransactionType.CREDIT);
        return request;
    }

    @Override
    public ElavonTransactionRequest createSale(final Transaction transaction) {
        final ElavonTransactionRequest request = createRequest(transaction);
        request.setTransactionType(ElavonTransactionType.SALE);
        return request;
    }

    @Override
    public ElavonTransactionRequest createVerify(final Transaction transaction) {
        final ElavonTransactionRequest request = createRequest(transaction);
        request.setTransactionType(ElavonTransactionType.VERIFY);
        return request;
    }

    /**
     * <pre><code>
     * Example Transaction:
     * {
     *   "action": "SALE",
     *   "amounts": {
     *   "currency": "USD",
     *   "orderAmount": 552,
     *   "tipAmount": 0,
     *   "transactionAmount": 552
     * },
     * "authOnly": false,
     * "context": {
     *   "businessId": "2ac806d1-73e7-40c3-94ec-be2bb401a2df",
     *   "businessType": "TEST_MERCHANT",
     *   "employeeUserId": 17371213,
     *   "mcc": "5812",
     *   "mid": "e10zu3b7xs",
     *   "source": "INSTORE",
     *   "sourceApp": "co.poynt.services",
     *   "storeAddressCity": "Palo Alto",
     *   "storeAddressTerritory": "California",
     *   "storeId": "992e7a4e-65e6-4919-825e-8b0f2f63a592",
     *   "storeTimezone": "America/Los_Angeles",
     *   "tid": "56uw"
     * },
     * "customerLanguage": "en",
     * "fundingSource": {
     *   "card": {
     *     "cardHolderFirstName": "2020",
     *     "cardHolderFullName": "MONEY/2020",
     *     "cardHolderLastName": "MONEY",
     *     "encrypted": true,
     *     "expirationDate": 31,
     *     "expirationMonth": 10,
     *     "expirationYear": 2016,
     *     "keySerialNumber": "FFFF9876543210E0004B",
     *     "numberFirst6": "453213",
     *     "numberLast4": "1054",
     *     "track1data": "CD7CF4B5497D239E977946B67A5364D894E4C72E015AD731E444B6ED3BACE67AE17CE1C542A76FA77B0E478ADB013BF034DF7C87307AD11A",
     *     "track2data": "",
     *     "type": "VISA"
     *   },
     *   "emvData": {
     *     "emvTags": {
     *     "0x5F24": "161031",
     *     "0x1F815D": "34",
     *     "0x5F20": "4D4F4E45592F32303230",
     *     "0x1F8104": "31303534",
     *     "0x1F815F": "04",
     *     "0x1F8103": "343533323133",
     *     "0x5F2A": "0840",
     *     "0x1F8102": "FFFF9876543210E0004B",
     *     "0x5F30": "101F",
     *     "0x1F8161": "00",
     *     "0x5F36": "02",
     *     "0x57": "",
     *     "0x58": "",
     *     "0x9F39": "02",
     *     "0x1F8153": "9D3E2DE7",
     *     "0x56": "CD7CF4B5497D239E977946B67A5364D894E4C72E015AD731E444B6ED3BACE67AE17CE1C542A76FA77B0E478ADB013BF034DF7C87307AD11A"
     *     }
     *   },
     *   "entryDetails": {
     *     "customerPresenceStatus": "PRESENT",
     *     "entryMode": "TRACK_DATA_FROM_MAGSTRIPE"
     *   },
     *   "type": "CREDIT_DEBIT"
     * },
     * "references": [
     *   {
     *     "customType": "referenceId",
     *     "id": "5c9a6b74-015f-1000-6146-0e9b4d0e4042",
     *     "type": "CUSTOM"
     *   }
     * ],
     * "signatureRequired": true
     * }
     * </code></pre>
     */
    private ElavonTransactionRequest createRequest(final Transaction t) {
        final ElavonTransactionRequest request = new ElavonTransactionRequest();
        FundingSourceEntryDetails entryDetails = t.getFundingSource().getEntryDetails();

        if (entryDetails.getEntryMode() == EntryMode.CONTACTLESS_INTEGRATED_CIRCUIT_CARD
                || entryDetails.getEntryMode() == EntryMode.CONTACTLESS_MAGSTRIPE) {
            request.setPosMode(ElavonPosMode.CL_CAPABLE);
            request.setEntryMode(ElavonEntryMode.CONTACTLESS);
        } else if (entryDetails.getEntryMode() == EntryMode.TRACK_DATA_FROM_MAGSTRIPE) {
            request.setPosMode(ElavonPosMode.SWIPE_CAPABLE);
            request.setEntryMode(ElavonEntryMode.SWIPED);
        }
        request.setAmount(CurrencyUtil.getAmount(t.getAmounts().getTransactionAmount(), t.getAmounts().getCurrency()));
        request.setFirstName(t.getFundingSource().getCard().getCardHolderFirstName());
        request.setLastName(t.getFundingSource().getCard().getCardHolderLastName());
        request.setEncryptedTrackData(t.getFundingSource().getCard().getTrack2data());
        request.setKsn(t.getFundingSource().getCard().getKeySerialNumber());
        request.setExpDate(CardUtil.getCardExpiry(t.getFundingSource().getCard()));
        request.setCardLast4(t.getFundingSource().getCard().getNumberLast4());
        if (t.getFundingSource().getVerificationData() != null) {
            request.setPinBlock(t.getFundingSource().getVerificationData().getPin());
            request.setPinKsn(t.getFundingSource().getVerificationData().getKeySerialNumber());
        }
        return request;
    }
}