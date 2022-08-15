/*
Anvil Community Contribution
Author: @dhildreth
*/

<?php

class AnvilDataService
{
    /**
     *  Basic steps:
     *    1. Split the string on the first colon (:)
     *    2. Decrypt the first part of the string with the private RSA key
     *    3. Use decrypted string from step 2 as the AES key to decrypt the second part
     *
     *  See also:
     *    - https://github.com/anvilco/node-encryption#rsa
     *    - https://github.com/anvilco/node-encryption/blob/master/src/index.js#L40-L50
     *
     * @throws Exception
     */
    public function decrypt(string $data): string
    {
        $index = strpos($data, ':');

        $encAesKey = substr($data, 0, $index);
        $encryptedMessage = substr($data, $index + 1);

        $aesKey = '';
        $ret = openssl_private_decrypt(
            data:           base64_decode($encAesKey),
            decrypted_data: $aesKey,
            private_key:    file_get_contents('/path/to/anvil-private-key.pem'),
            padding:        OPENSSL_PKCS1_OAEP_PADDING
        );

        if(!$ret) {
            throw new Exception('Unable to private decrypt AES key');
        }

        return $this->decryptAES($aesKey, $encryptedMessage);
    }

    /**
     * @throws Exception
     */
    private function decryptAES(string $key, string $message): string
    {
        $algorithm = 'aes-256-cbc';

        $textParts = explode(':', $message);
        $iv = hex2bin(array_shift($textParts));
        $encryptedText = hex2bin(implode(':', $textParts));
        $key = hex2bin($key);

        $decrypted = openssl_decrypt(
            data: $encryptedText,
            cipher_algo: $algorithm,
            passphrase: $key,
            options: OPENSSL_RAW_DATA|OPENSSL_ZERO_PADDING,
            iv: $iv
        );

        if(!$decrypted) {
            throw new Exception('Unable to decrypt AES message');
        }

        /**
         * The 'SOH' (Start of Header) control character is inserted somewhere
         * in the hex2bin conversion, so remove it and potential others.
         */
        return trim($decrypted, "\x00.. \x20");
    }
}
