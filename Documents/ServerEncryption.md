# ServerEncryption Class

## Short Descriptive Summary
The `ServerEncryption` class provides methods for RSA and AES encryption and decryption within a server-client communication context.

## Why?
This class exists to facilitate secure communication between a server and its clients by implementing both RSA and AES encryption and decryption. RSA is used for normal data exchange like username and password sending , but AES is used for encrypting video file to prevent overhead.

## Drawbacks
One potential drawback of using RSA encryption is its computational overhead, especially with larger key sizes. While RSA provides strong security guarantees, it can be relatively slow compared to symmetric encryption algorithms for large volumes of data. so because of that we use AES encryption algorithm to prevent this .

## Architecture
- The `ServerEncryption` class constructor runs one time per server class run . This class generates a new RSA key pair (public and private) upon instantiation. At the beginning of each connection to the server, it sends its public key and receives the client's public key. The latest public key of the user will be saved in the database.
- The class utilizes RSA encryption for key exchange and important data like username and passsword and AES encryption for securing data transmission. It provides methods to encrypt and decrypt data using both RSA and AES algorithms.

### Methods
- `generateRSAkeyPair()`: Generates an RSA key pair (public-private) with a key size of 2048 bits.
- `encryptDataRSA(String jsonData, PublicKey clientPublicKey)`: Encrypts the provided JSON data using the client's public key and returns the encrypted data as a Base64-encoded string.
- `decryptDataRSA(String encryptedJsonData)`: Decrypts the provided encrypted data using the server's private key and returns the original JSON string.
- `encryptDataAES(byte[] fileBytes, SecretKey secretKey)`: Encrypts the provided  file bytes array using the given AES secret key and returns the encrypted data as a byte array. **This function is just used for data transmission**
- `decryptDataAES(byte[] encryptedFileBytes, SecretKey secretKey)`: Decrypts the provided encrypted byte array using the given AES secret key and returns the original byte array. **This function is just used for data transmission**
- `generateAESsecretKey()`: Generates a new AES secret key with a key size of 128 bits.
- `getServerRSApublicKey()`: Returns the server's RSA public key.

### Security Risks
Note that there is no `getServerPrivateKey()` function for security reasons. Exposing the server's private key could lead to severe security vulnerabilities. Always handle private keys with utmost care and avoid exposing them unnecessarily.


