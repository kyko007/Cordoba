dm2_2 <- kmeans(dim2, 2, nstart = 1000, iter.max = 10000)
dm2_3 <- kmeans(dim2, 3, nstart = 1000, iter.max = 10000)
dm2_4 <- kmeans(dim2, 4, nstart = 1000, iter.max = 10000)
dm2_5 <- kmeans(dim2, 5, nstart = 1000, iter.max = 10000)
dm2_6 <- kmeans(dim2, 6, nstart = 1000, iter.max = 10000)
dm2_7 <- kmeans(dim2, 7, nstart = 1000, iter.max = 10000)
dm2_8 <- kmeans(dim2, 8, nstart = 1000, iter.max = 10000)
dm2_9 <- kmeans(dim2, 9, nstart = 1000, iter.max = 10000)
dm2_10 <- kmeans(dim2, 10, nstart = 1000, iter.max = 10000)
dm2_11 <- kmeans(dim2, 11, nstart = 1000, iter.max = 10000)
x2 <- rbind(dm2_2, dm2_3, dm2_4, dm2_5, dm2_6, dm2_7, dm2_8, dm2_9, dm2_10, dm2_11)

indexes <- ["C_index", "Calinski_Harabasz", "Davies_Bouldin", "Dunn", "Gamma", "G_plus", "PBM", "Ray_Turi", "S_Dbw", "Silhouette", "Tau", "Wemmert_Gancarski", "Xie_Beni"]

x3 <-{}
dim2numeric <- dim2 * 1.0
x3 <- rbind(x3, intCriteria(data.matrix(dim2numeric), dm2_2$cluster, indexes))

x3 <- rbind(x3, intCriteria(data.matrix(dim2numeric), dm2_3$cluster, indexes))

x3 <- rbind(x3, intCriteria(data.matrix(dim2numeric), dm2_4$cluster, indexes))

x3 <- rbind(x3, intCriteria(data.matrix(dim2numeric), dm2_5$cluster, indexes))

x3 <- rbind(x3, intCriteria(data.matrix(dim2numeric), dm2_6$cluster, indexes))

x3 <- rbind(x3, intCriteria(data.matrix(dim2numeric), dm2_7$cluster, indexes))

x3 <- rbind(x3, intCriteria(data.matrix(dim2numeric), dm2_8$cluster, indexes))

x3 <- rbind(x3, intCriteria(data.matrix(dim2numeric), dm2_9$cluster, indexes))

x3 <- rbind(x3, intCriteria(data.matrix(dim2numeric), dm2_10$cluster, indexes))

x3 <- rbind(x3, intCriteria(data.matrix(dim2numeric), dm2_11$cluster, indexes))

head(x3)
