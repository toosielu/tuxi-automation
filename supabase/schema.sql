create extension if not exists "pgcrypto";

create table if not exists users (
    id uuid primary key default gen_random_uuid(),
    supabase_user_id uuid unique,
    nickname text,
    created_at timestamptz not null default now()
);

create table if not exists projects (
    id uuid primary key default gen_random_uuid(),
    user_id uuid references users(id) on delete set null,
    name text not null,
    niche text,
    audience text,
    stage text,
    goal text,
    pain text,
    daily_minutes integer not null default 30,
    budget integer not null default 0,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create table if not exists niche_analysis (
    id uuid primary key default gen_random_uuid(),
    project_id uuid not null references projects(id) on delete cascade,
    total_score integer not null,
    demand_score integer not null,
    monetization_score integer not null,
    execution_score integer not null,
    budget_fit_score integer not null,
    risk_score integer not null,
    level text not null,
    summary text not null,
    raw_result_json jsonb not null default '{}'::jsonb,
    created_at timestamptz not null default now()
);

create table if not exists products (
    id uuid primary key default gen_random_uuid(),
    name text not null,
    category text not null,
    audience jsonb not null default '[]'::jsonb,
    type text not null,
    price_range text,
    delivery text,
    difficulty text,
    risk text,
    selling_points jsonb not null default '[]'::jsonb,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create table if not exists generated_posts (
    id uuid primary key default gen_random_uuid(),
    project_id uuid not null references projects(id) on delete cascade,
    product_id uuid references products(id) on delete set null,
    angle text,
    title text not null,
    cover_text text,
    body text not null,
    cta text,
    image_prompt text,
    status text not null default 'draft',
    created_at timestamptz not null default now()
);

create table if not exists publish_schedules (
    id uuid primary key default gen_random_uuid(),
    post_id uuid not null references generated_posts(id) on delete cascade,
    platform text not null default 'xiaohongshu',
    scheduled_at timestamptz,
    status text not null default 'pending',
    published_url text,
    notes text,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create table if not exists post_metrics (
    id uuid primary key default gen_random_uuid(),
    post_id uuid not null references generated_posts(id) on delete cascade,
    impressions integer not null default 0,
    likes integer not null default 0,
    saves integer not null default 0,
    comments integer not null default 0,
    direct_messages integer not null default 0,
    store_clicks integer not null default 0,
    orders integer not null default 0,
    created_at timestamptz not null default now()
);

create table if not exists ai_generation_logs (
    id uuid primary key default gen_random_uuid(),
    project_id uuid references projects(id) on delete cascade,
    generation_type text not null,
    prompt text,
    result_json jsonb not null default '{}'::jsonb,
    provider text,
    model text,
    created_at timestamptz not null default now()
);

create index if not exists idx_projects_user_id on projects(user_id);
create index if not exists idx_niche_analysis_project_id on niche_analysis(project_id);
create index if not exists idx_generated_posts_project_id on generated_posts(project_id);
create index if not exists idx_publish_schedules_post_id on publish_schedules(post_id);
create index if not exists idx_post_metrics_post_id on post_metrics(post_id);
