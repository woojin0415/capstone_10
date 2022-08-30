#autoencoder Architecture(input part)
input_img = Input(shape=(150, 50, 1))

#autoencoder Architecture(encoder part)
x = Conv2D(32, (9, 3), activation='relu', padding='same')(input_img)
x = MaxPooling2D((6, 2), padding='same')(x)
x = Conv2D(32, (9, 3), activation='relu', padding='same')(x)
encoded = MaxPooling2D((6, 2), padding='same')(x)

#autoencoder Architecture(decoder part)
x = Conv2D(32, (9, 3), activation='relu', padding='same')(encoded)
x = UpSampling2D((6, 2))(x)
x = Conv2D(32, (9, 3), activation='relu', padding='same')(x)
x = UpSampling2D((6, 2))(x)
x = Flatten()(x)
x = Dense(512, activation='relu')(x)
x = Dense(128, activation='relu')(x)
x = Dense(32, activation='relu')(x)
decoded = Dense(4, activation='sigmoid')(x)
#Modeling autoencoder
machine = Model(input_img, decoded)


#Compiling Autoencoder
machine.compile(optimizer='adam', loss='binary_crossentropy')

#Training Autoencoder
machine.fit(train_data, train_data_correct,
                batch_size=100, epochs=50,
