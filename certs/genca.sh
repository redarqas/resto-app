export PW=`cat password`

# Create a self signed key pair root CA certificate.
keytool -genkeypair -v \
  -alias novapostca \
  -dname "CN=novapostCA, OU=Novapost Org, O=Novapost Company, L=Paris, ST=idf, C=FR" \
  -keystore novapostca.jks \
  -keypass:env PW \
  -storepass:env PW \
  -keyalg RSA \
  -keysize 4096 \
  -ext KeyUsage:critical="keyCertSign" \
  -ext BasicConstraints:critical="ca:true" \
  -validity 9999

# Export the novapostCA public certificate as novapostca.crt so that it can be used in trust stores.
keytool -export -v \
  -alias novapostca \
  -file novapostca.crt \
  -keypass:env PW \
  -storepass:env PW \
  -keystore novapostca.jks \
  -rfc


