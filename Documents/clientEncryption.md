# ClientEncryption Class

## Short Descriptive Summary
The `ClientEncryption` class provides methods for RSA and AES encryption and decryption within a client-server communication context.

## Why?
This class exists to enable secure communication between a client and a server by implementing both RSA and AES encryption and decryption. RSA is used for important data exchange like username and password sending , but AES is used for encrypting video file to prevent overhead.

## Drawbacks
Similar to the server-side implementation, the computational overhead of RSA encryption may be a drawback, particularly with larger key sizes. While RSA offers strong security guarantees, its performance can be relatively slower compared to symmetric encryption algorithms for large datasets. so because of that we use AES encryption algorithm to prevent this .


## Architecture
- The `ClientEncryption` class constructor runs for each client app run no matter whether the user wants to log in or sign up. This class generates a new RSA key pair (public and private) upon instantiation. At the beginning of each connection to the server, it sends its public key and receives the server's public key. The latest public key of the user will be saved in the database.
- The class utilizes RSA encryption for key exchange and important data like username and password and AES encryption for securing data transmission. It provides methods to encrypt and decrypt data using both RSA and AES algorithms.

### Methods
- `generateRSAkeyPair()`: Generates an RSA key pair (public-private) with a key size of 2048 bits.
- `encryptDataRSA(String jsonData, PublicKey serverPublicKey)`: Encrypts the provided JSON data using the server's public key and returns the encrypted data as a Base64-encoded string.
- `decryptDataRSA(String encryptedJsonData)`: Decrypts the provided encrypted data using the client's private key and returns the original JSON string.
- `encryptDataAES(byte[] fileBytes, SecretKey secretKey)`: Encrypts the provided byte array using the given AES secret key and returns the encrypted data as a byte array. **This function is just used for data transmission**
- `decryptDataAES(byte[] encryptedFileBytes, SecretKey secretKey)`: Decrypts the provided encrypted byte array using the given AES secret key and returns the original byte array. **This function is just used for data transmission**
- `getClientRSApublicKey()`: Returns the client's RSA public key.

### Security Risks
Note that there is no `getClientPrivateKey()` function for security reasons. Exposing the client's private key could lead to severe security vulnerabilities. Always handle private keys with utmost care and avoid exposing them unnecessarily.
