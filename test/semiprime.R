library(dplyr)
library(ggplot2)
library(plotly)

file <- "05-01-2019.seed-1.hills.min-20.max-20.runs-1.csv"
file <- "steps.csv"
file <- "elapsed.csv"
file <- "mod.csv"
file <- "error.csv"

files <- list.files("C:/src/semiprime/test", pattern="*shapes*", full.names=FALSE)
file <- files[2]

csv <- read.csv(paste("C:/src/semiprime/test/", file, sep=""))

# mod
mod <- ggplot(csv, aes(1:nrow(csv), mod)) + geom_point() + geom_smooth()
mod + labs(x = "steps", y = "(x + y) mod z")

# error of min(p,q) / initial z
ggplot(csv, aes(error)) + geom_histogram()


# zmod vs smod
mod <- ggplot(csv, aes(zmod, smod)) + geom_point() + geom_smooth()
mod + labs(x = "zmod", y = "smod")

# shapes
mod <- ggplot(csv, aes(1:nrow(csv), csv[,1])) + geom_point() + geom_smooth()
mod + labs(x ="iteration", y = "(x+y) mod z")
csv %>% filter(mod < 100)
head(csv)

# stats
sort(csv[,1])[1:10]
steps <- sort(csv[,3])
df <- data.frame(1:nrow(csv), steps)
ggplot(df, aes(df[,1], df[,2])) + geom_point() + geom_smooth()
ggplot(csv, aes(len, elapsed)) + geom_point() + geom_smooth()

# elapsed
ggplot(csv, aes(1:nrow(csv), elapsed)) + geom_point() + geom_smooth()

# steps
ggplot(csv, aes(1:nrow(csv), steps)) + geom_point() + geom_smooth()
ggplot(csv, aes(steps)) + geom_histogram()

# hills
head(csv)
h <- ggplot(csv, aes(1:nrow(csv), csv[,1])) +  geom_point() + geom_smooth()
h + labs(x = "Search Progress", y = "Distance from Goal")

# pairs
head(csv) 
surf <- data.matrix(csv)
p <- plot_ly(csv, z = surf) %>% add_surface()



df <- data.frame(x = 1:100, y = 500:599, z = sample(1:200, size = 100))
p <- plot_ly(df, x = df$x, y = df$y, z = df$z) %>% add_surface()
p

hills <- data.frame(1:nrow(csv), csv[,3])
ggplot(hills, aes(hills[,1], hills[,2])) +  geom_point() + geom_smooth()

ggplot(csv, aes(expanded)) + geom_histogram() + xlim(0, 65536)

ggplot(csv, aes(len, expanded)) +  geom_point() + geom_smooth()

csv %>% 
  select(expanded) %>% 
  summarize(
    min(expanded),
    max(expanded),
    mean(expanded)
    )

x <- select(csv, open.size)
y <- rownames(x)