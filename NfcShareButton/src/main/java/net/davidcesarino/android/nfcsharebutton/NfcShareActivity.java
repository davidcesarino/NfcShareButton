/*
 * Copyright 2013 David Cesarino de Sousa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.davidcesarino.android.nfcsharebutton;

import android.app.Activity;
import android.content.Intent;
import android.nfc.NdefMessage;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;

public class NfcShareActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent i = getShareIntent(getIntent());
        if (i != null) {
            startActivity(Intent.createChooser(i, getString(R.string.share_with)));
        }
        finish();
    }

    @Override
    public void onNewIntent(Intent intent) {
        setIntent(intent);
    }

    /**
     * <p>Crude method to convert an NFC {@link Intent} to a regular {@code Intent}
     * suitable to share text in Android.</p>
     *
     * @param intent the {@code Intent} as arrived from the system.
     * @return a regular {@code Intent} suitable for normal {@code ACTION_SEND}.
     */
    private Intent getShareIntent(Intent intent) {
        if (intent != null
                && intent.getAction() != null
                && NfcAdapter.ACTION_NDEF_DISCOVERED.equals(intent.getAction())) {
            // TODO Safely remove ISO-639 language code. Maybe check NFCForum-TS-RTD_1.0 ?
            Parcelable[] parcelMessages = intent
                    .getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            if (parcelMessages != null && parcelMessages.length > 0) {
                NdefMessage ndefMessage = (NdefMessage) parcelMessages[0];
                if (ndefMessage != null && ndefMessage.getRecords().length > 0) {
                    byte[] bytes = ndefMessage.getRecords()[0].getPayload();
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, new String(bytes));
                    return shareIntent;
                } else return null;
            } else return null;
        } else return null;
    }

}
