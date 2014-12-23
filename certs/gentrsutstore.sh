export PW=`cat password`


# Create a JKS keystore that trusts the novapost CA, with the default password.
keytool -import -v \
  -alias novapostca \
  -file novapostca.crt \
  -keypass:env PW \
  -storepass changeit \
  -keystore exampletrust.jks << EOF
oui
EOF

# List out the details of the store password.
keytool -list -v \
  -keystore exampletrust.jks \
  -storepass changeit