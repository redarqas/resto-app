export PW=`cat password`

# Create a server certificate, tied to example.com
keytool -genkeypair -v \
  -alias example.com \
  -dname "CN=example.com, OU=Novapost Org, O=Novapost Company, L=Paris, ST=idf, C=FR" \
  -keystore example.com.jks \
  -keypass:env PW \
  -storepass:env PW \
  -keyalg RSA \
  -keysize 2048 \
  -validity 385

# Create a certificate signing request for example.com
keytool -certreq -v \
  -alias example.com \
  -keypass:env PW \
  -storepass:env PW \
  -keystore example.com.jks \
  -file example.com.csr

# Tell exampleCA to sign the example.com certificate. Note the extension is on the request, not the
# original certificate.
# Technically, keyUsage should be digitalSignature for DHE or ECDHE, keyEncipherment for RSA.
keytool -gencert -v \
  -alias novapostca \
  -keypass:env PW \
  -storepass:env PW \
  -keystore novapostca.jks \
  -infile example.com.csr \
  -outfile example.com.crt \
  -ext KeyUsage:critical="digitalSignature,keyEncipherment" \
  -ext EKU="serverAuth" \
  -ext SAN="DNS:example.com" \
  -rfc

# Tell example.com.jks it can trust novapostca as a signer.
keytool -import -v \
  -alias novapostca \
  -file novapostca.crt \
  -keystore example.com.jks \
  -storetype JKS \
  -storepass:env PW << EOF
oui
EOF

# Import the signed certificate back into example.com.jks 
keytool -import -v \
  -alias example.com \
  -file example.com.crt \
  -keystore example.com.jks \
  -storetype JKS \
  -storepass:env PW

# List out the contents of example.com.jks just to confirm it.  
# If you are using Play as a TLS termination point, this is the key store you should present as the server.
keytool -list -v \
  -keystore example.com.jks \
  -storepass:env PW

