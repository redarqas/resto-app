export PW=`cat ../password`

# On a besoin du certificat du CA serveur

# Create a JKS keystore that trusts the example CA, with the default password.
keytool -import -v \
  -alias exampleca \
  -file exampleca.crt \
  -keypass:env PW \
  -storepass changeit \
  -keystore exampletrust.jks << EOF
oui
EOF