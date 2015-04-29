for pic in imagenes/*.ppm
do
pnmtojpeg "${pic}" > "${pic/%ppm/jpg}"
done
