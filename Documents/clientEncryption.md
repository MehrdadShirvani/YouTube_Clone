# ClientEncryption Class

## Short Descriptive Summary
The `ClientEncryption` class provides methods for RSA encryption and decryption within a client-server communication context.

## Why?
This class exists to enable secure communication between a client and a server by implementing RSA encryption and decryption. RSA is an asymmetric encryption algorithm widely used for securing data transmission, ensuring confidentiality and integrity.

## Drawbacks
Similar to the server-side implementation, the computational overhead of RSA encryption may be a drawback, particularly with larger key sizes. While RSA offers strong security guarantees, its performance can be relatively slower compared to symmetric encryption algorithms for large datasets.

## Architecture
*  The `clientEncryption` class constructor will run for each client app run no matter whether the user wants to log in or sign up.
    This class will generate a new public and private key pair each time. At the beginning of each connection to the server,
    it sends its public key and receives the server's public key. The latest public key of the user will be saved in the database.
* The `ClientEncryption` class utilizes RSA encryption to secure data transmission between the client and server. Upon instantiation, it generates a key pair consisting of a public key (for encryption) and a private key (for decryption). The `encryptData` method accepts a JSON string and the server's public key, encrypts the data using RSA, and returns the encrypted data as a Base64-encoded string. The `decryptData` method decrypts the encrypted data using the client's private key and returns the original JSON string.
* Finally note that there is no any getClientPriavteKey function because of the **Security Risks** .

### Methods
- `generateKeyPair()`: Generates an RSA key pair (public-private) with a key size of 2048 bits.
- `encryptData(String jsonData, PublicKey serverPublicKey)`: Encrypts the provided JSON data using the server's public key and returns the encrypted data as a Base64-encoded string.
- `decryptData(String encryptedJsonData)`: Decrypts the provided encrypted data using the client's private key and returns the original JSON string.
- `getClientPublicKey()`: Returns the client's public key.

