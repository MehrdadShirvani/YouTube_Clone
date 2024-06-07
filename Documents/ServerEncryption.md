# ServerEncryption Class

## Short Descriptive Summary
The `ServerEncryption` class provides methods for RSA encryption and decryption within a server-client communication context.

## Why?
This class exists to facilitate secure communication between a server and its clients by implementing RSA encryption and decryption.

## Drawbacks
One potential drawback of using RSA encryption is its computational overhead, especially with larger key sizes. While RSA provides strong security guarantees, it can be relatively slow compared to symmetric encryption algorithms for large volumes of data.

## Architecture
* The `ServerEncryption` class constructor will run for each client app run no matter whether the user wants to log in or sign up.
 This class will generate a new public and private key pair each time. At the beginning of each connection to the server,
 it sends its public key and receives the server's public key. The latest public key of the user will be saved in the database.
* The `ServerEncryption` class utilizes RSA encryption for securing data transmission. It generates a key pair upon instantiation, consisting of a public key (for encryption) and a private key (for decryption). The `encryptData` method takes a JSON string and a client's public key as input, encrypts the data using RSA, and returns the encrypted data as a Base64-encoded string. The `decryptData` method decrypts the encrypted data using the server's private key and returns the original JSON string.
* Finally note that there is no any getServerPrivateKey function because of the **Security Risks** .

### Methods
- `generateKeyPair()`: Generates an RSA key pair (public-private) with a key size of 2048 bits.
- `encryptData(String jsonData, PublicKey clientPublicKey)`: Encrypts the provided JSON data using the client's public key and returns the encrypted data as a Base64-encoded string.
- `decryptData(String encryptedJsonData)`: Decrypts the provided encrypted data using the server's private key and returns the original JSON string.
- `getServerPublicKey()`: Returns the server's public key.

